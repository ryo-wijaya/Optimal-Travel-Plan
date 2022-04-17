import { PaymentAccount } from "./payment-account";
import { Customer } from "./customer";

export class PaymentAccountHandler {
    paymentAccount: PaymentAccount | undefined;
    customer: Customer | undefined;
    password: string | undefined;
    date: string | undefined;
    paymentAccountId: number | undefined;

    constructor(paymentAccount?: PaymentAccount, customer?: Customer,
        password?: string, date?: string) {
            this.paymentAccount = paymentAccount;
            this.customer = customer;
            this.password = password;
            this.date = date;
    }
}
