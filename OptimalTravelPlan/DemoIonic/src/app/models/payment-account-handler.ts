import { PaymentAccount } from "./payment-account";
import { Customer } from "./customer";

export class PaymentAccountHandler {
    paymentAccount: PaymentAccount | undefined;
    customer: Customer | undefined;
    password: string | undefined;

    constructor(paymentAccount?: PaymentAccount, customer?: Customer,
        password?: string) {
            this.paymentAccount = paymentAccount;
            this.customer = customer;
            this.password = password;
    }
}
