
import mysql.connector
import snowflake.connector
import pandas as pd
import os
from dotenv import load_dotenv

load_dotenv()



MYSQL_CONFIG = {
    'user': os.getenv('MYSQL_USER'),
    'password': os.getenv('MYSQL_PASSWORD'),
    'host': os.getenv('MYSQL_HOST'),
    'database': os.getenv('MYSQL_DB')
}

SNOWFLAKE_CONFIG = {
    'user': os.getenv('SF_USER'),
    'password': os.getenv('SF_PASSWORD'),
    'account': os.getenv('SF_ACCOUNT'),
    'warehouse': os.getenv('SF_WAREHOUSE'),
    'database': os.getenv('SF_DATABASE'),
    'schema': os.getenv('SF_SCHEMA')
}



ACCOUNTS_CSV = 'accounts_extract.csv'
TRANSACTIONS_CSV = 'transactions_extract.csv'

# def extract_from_mysql():
#     print("🚀 [1/3] Extracting data from MySQL...")
#     conn = mysql.connector.connect(**MYSQL_CONFIG)
    
#     # 1. Extract Accounts
#     # We select specific columns to match your DIM_ACCOUNT logic
#     sql_accounts = "SELECT id, holder_name, email, balance, status, last_updated FROM accounts"
#     df_accounts = pd.read_sql(sql_accounts, conn)
#     # Clean data: Ensure no newlines in text fields
#     df_accounts = df_accounts.replace(r'\n', ' ', regex=True)
#     df_accounts.to_csv(ACCOUNTS_CSV, index=False, header=False) # No header for Snowflake COPY
#     print(f"   -> Extracted {len(df_accounts)} accounts.")

#     # 2. Extract Transaction Logs
#     # Note: We read the raw numeric 'amount' here, so we don't need regex in Snowflake!
#     sql_txns = "SELECT from_account_id, to_account_id, amount, status, created_on, idempotency_key FROM transaction_logs"
#     df_txns = pd.read_sql(sql_txns, conn)
#     df_txns.to_csv(TRANSACTIONS_CSV, index=False, header=False)
#     print(f"   -> Extracted {len(df_txns)} transactions.")
    
#     conn.close()
def extract_from_mysql():
    print("🚀 [1/3] Extracting data from MySQL...")
    conn = mysql.connector.connect(**MYSQL_CONFIG)
    
    
    sql_accounts = "SELECT id, holder_name, email, balance, status, last_updated FROM accounts"
    df_accounts = pd.read_sql(sql_accounts, conn)
    
    
    df_accounts = df_accounts.replace(r'\n', ' ', regex=True)
    
    
    df_accounts.to_csv(ACCOUNTS_CSV, index=False, header=True) 
    print(f"   -> Extracted {len(df_accounts)} accounts.")

   
    sql_txns = "SELECT from_account_id, to_account_id, amount, status, created_on, idempotency_key FROM transaction_logs"
    df_txns = pd.read_sql(sql_txns, conn)
    
    
    df_txns.to_csv(TRANSACTIONS_CSV, index=False, header=True)
    print(f"   -> Extracted {len(df_txns)} transactions.")
    
    conn.close()
    
def upload_to_snowflake(ctx):
    print("🚀 [2/3] Uploading to Snowflake Stage...")
    cs = ctx.cursor()
    
    
    try:
        cs.execute(f"PUT file://{os.path.abspath(ACCOUNTS_CSV)} @TRANSFER_STAGE OVERWRITE=TRUE")
        cs.execute(f"PUT file://{os.path.abspath(TRANSACTIONS_CSV)} @TRANSFER_STAGE OVERWRITE=TRUE")
        print("   -> Upload successful.")
    finally:
        cs.close()


# def load_into_snowflake(ctx):
#     print("🚀 [3/3] Loading Data into Tables...")
#     cs = ctx.cursor()

#     try:
#         # --- A. Load DIM_ACCOUNT ---
#         print("   -> Loading DIM_ACCOUNT (Full Refresh)...")
#         cs.execute("TRUNCATE TABLE DIM_ACCOUNT")
        
#         sql_load_accounts = """
#         COPY INTO DIM_ACCOUNT (ACCOUNT_ID, HOLDER_NAME, EMAIL, STATUS, EFFECTIVE_DATE)
#         FROM (
#             SELECT t.$1, t.$2, t.$3, t.$5, CURRENT_DATE()
#             FROM @TRANSFER_STAGE/accounts_extract.csv.gz t
#         )
#         FILE_FORMAT = (FORMAT_NAME = 'CSV_GENERIC_FORMAT')
#         """
#         cs.execute(sql_load_accounts)

#         # --- B. Load FACT_TRANSACTIONS ---
#         print("   -> Loading FACT_TRANSACTIONS (Full Refresh)...")
#         cs.execute("TRUNCATE TABLE FACT_TRANSACTIONS")

#         # FIXED: We now insert '1' as a placeholder for DATE_KEY
#         sql_load_facts = """
#         INSERT INTO FACT_TRANSACTIONS (ACCOUNT_FROM_KEY, ACCOUNT_TO_KEY, AMOUNT, STATUS, CREATED_AT, DATE_KEY)
#         SELECT 
#             COALESCE(da_from.ACCOUNT_KEY, -1),
#             COALESCE(da_to.ACCOUNT_KEY, -1),
#             t.$3, 
#             t.$4, 
#             TO_TIMESTAMP_NTZ(t.$5),
#             1  -- <--- THE FIX: Insert Key 1 temporarily (Jan 1, 2025)
#         FROM @TRANSFER_STAGE/transactions_extract.csv.gz t
#         LEFT JOIN DIM_ACCOUNT da_from ON da_from.ACCOUNT_ID = t.$1
#         LEFT JOIN DIM_ACCOUNT da_to   ON da_to.ACCOUNT_ID   = t.$2
#         """
#         cs.execute(sql_load_facts)

#         # --- C. AUTO-FIX DATES ---
#         print("   -> Linking Dates...")
#         # This instantly overwrites the '1' with the correct date key
#         sql_fix_dates = """
#         UPDATE FACT_TRANSACTIONS f
#         SET f.DATE_KEY = d.DATE_KEY
#         FROM DIM_DATE d
#         WHERE TO_DATE(f.CREATED_AT) = d.FULL_DATE
#         """
#         cs.execute(sql_fix_dates)
        
#         print("   -> Loading Complete!")

#     finally:
#         cs.close()
def load_into_snowflake(ctx):
    print("🚀 [3/3] Loading Data into Tables...")
    cs = ctx.cursor()

    try:
        
        print("   -> Loading DIM_ACCOUNT (Full Refresh)...")
        cs.execute("TRUNCATE TABLE DIM_ACCOUNT")
        
        sql_load_accounts = """
        COPY INTO DIM_ACCOUNT (ACCOUNT_ID, HOLDER_NAME, EMAIL, STATUS, EFFECTIVE_DATE)
        FROM (
            SELECT 
                t.$1, 
                t.$2, 
                t.$3, 
                t.$5, 
                CURRENT_DATE() 
            FROM @TRANSFER_STAGE/accounts_extract.csv.gz t
        )
        FILE_FORMAT = (FORMAT_NAME = 'CSV_GENERIC_FORMAT')
        """
        cs.execute(sql_load_accounts)

        
        print("   -> Loading FACT_TRANSACTIONS (Full Refresh)...")
        cs.execute("TRUNCATE TABLE FACT_TRANSACTIONS")

        
        sql_load_facts = """
        INSERT INTO FACT_TRANSACTIONS (ACCOUNT_FROM_KEY, ACCOUNT_TO_KEY, AMOUNT, STATUS, CREATED_AT, DATE_KEY)
        SELECT 
            COALESCE(da_from.ACCOUNT_KEY, -1),
            COALESCE(da_to.ACCOUNT_KEY, -1),
            t.$3, 
            t.$4, 
            TO_TIMESTAMP_NTZ(t.$5),
            1  
        FROM @TRANSFER_STAGE/transactions_extract.csv.gz t
        
        LEFT JOIN DIM_ACCOUNT da_from ON da_from.ACCOUNT_ID = TRY_TO_NUMBER(t.$1) 
        LEFT JOIN DIM_ACCOUNT da_to   ON da_to.ACCOUNT_ID   = TRY_TO_NUMBER(t.$2)
        """
        cs.execute(sql_load_facts)

        
        print("   -> Linking Dates...")
        sql_fix_dates = """
        UPDATE FACT_TRANSACTIONS f
        SET f.DATE_KEY = d.DATE_KEY
        FROM DIM_DATE d
        WHERE TO_DATE(f.CREATED_AT) = d.FULL_DATE
        """
        cs.execute(sql_fix_dates)
        
        print("   -> Loading Complete!")

    finally:
        cs.close()

def main():
    
    ctx = snowflake.connector.connect(**SNOWFLAKE_CONFIG)
    
    try:
        extract_from_mysql()
        upload_to_snowflake(ctx)
        load_into_snowflake(ctx)
        print("✅ Pipeline Finished Successfully.")
    except Exception as e:
        print(f"❌ Error: {e}")
    finally:
        ctx.close()
        
        if os.path.exists(ACCOUNTS_CSV): os.remove(ACCOUNTS_CSV)
        if os.path.exists(TRANSACTIONS_CSV): os.remove(TRANSACTIONS_CSV)

if __name__ == "__main__":
    main()