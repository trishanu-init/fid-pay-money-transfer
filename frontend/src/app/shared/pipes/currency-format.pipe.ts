import { Pipe, PipeTransform } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Currency Pipe
 * 
 * Formats numbers as currency with the configured symbol.
 */
@Pipe({
    name: 'appCurrency'
})
export class CurrencyFormatPipe implements PipeTransform {
    transform(value: number | null | undefined, showSymbol = true): string {
        if (value === null || value === undefined) {
            return showSymbol ? `${environment.currencySymbol}0.00` : '0.00';
        }

        const formattedValue = new Intl.NumberFormat('en-IN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(value);

        return showSymbol ? `${environment.currencySymbol}${formattedValue}` : formattedValue;
    }
}
