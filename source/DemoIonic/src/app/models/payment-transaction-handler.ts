import { PaymentTransaction } from "./payment-transaction";
import { Customer } from "./customer";

export class PaymentTransactionHandler {
    paymentTransaction: PaymentTransaction | undefined;
    customer: Customer | undefined;
    bookingId: number | undefined | null;
    paymentAccountId: number | undefined | null;
    password: string | undefined;

    constructor(paymentTransaction?: PaymentTransaction, customer?: Customer,
        bookingId?: number, paymentAccountId?: number,
        password?: string) {
            this.paymentTransaction = paymentTransaction;
            this.customer = customer;
            this.bookingId = bookingId;
            this.paymentAccountId = paymentAccountId;
            this.password = password;
    }


}
