/**
 * Account model representing user account details
 */
export interface Account {
    accountId: number;
    holderName: string;
    balance: number;
    status: AccountStatus;
    lastUpdated: string;
}

export type AccountStatus = 'ACTIVE' | 'LOCKED' | 'CLOSED';

/**
 * Transaction log model for transaction history
 */
export interface TransactionLog {
    id: string;
    fromAccountId: number;
    toAccountId: number;
    amount: number;
    status: TransactionStatus;
    failureReason?: string;
    idempotencyKey: string;
    createdOn: string;
}

export type TransactionStatus = 'SUCCESS' | 'FAILED';

export interface TransferRequest {
    fromAccountId: number;
    toAccountId: number;
    amount: number;
    idempotencyKey: string;
}

/**
 * Transfer response from API
 */
export interface TransferResponse {
    transactionId: string;
    status: string;
    message: string;
    debitedFromAccountId: number;
    creditedToAccountId: number;
    amount: number;
}

/**
 * Login credentials
 */
export interface LoginCredentials {
    email: string;
    password: string;
}

/**
 * Auth token response
 */
export interface AuthResponse {
    token: string;
    tokenType: string;
    email: string;
    message: string;
    expiresIn: number;
    accountId: number;
}

/**
 * Error response from API
 */
export interface ApiError {
    status: number;
    message: string;
    timestamp: string;
}

/**
 * OTP Request for sending OTP
 */
export interface OtpRequest {
    accountId: number;
}

/**
 * OTP Verify Request
 */
export interface OtpVerifyRequest {
    accountId: number;
    otp: string;
}

/**
 * OTP Response
 */
export interface OtpResponse {
    success: boolean;
    message: string;
}

/**
 * Paginated response from Spring Boot Page<T>
 */
export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;  // current page (0-indexed)
    size: number;
    first: boolean;
    last: boolean;
}

