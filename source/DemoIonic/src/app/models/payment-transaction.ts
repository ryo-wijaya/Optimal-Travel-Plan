import { PaymentAccount } from "./payment-account";

export class PaymentTransaction {

    paymentTransactionId: number | undefined;
    paymentAccount: PaymentAccount | undefined;
    dateOfPayment: Date | undefined;
    transactionNumber: string | undefined;
    prevailingRateAtPaymentDate: number | undefined;

    constructor(paymentTransactionId?: number, paymentAccount?: PaymentAccount,
        dateOfPayment?: Date, transactionNumber?: string,
        prevailingRateAtPaymentDate?: number) {
            this.paymentTransactionId = paymentTransactionId;
            this.paymentAccount = paymentAccount;
            this.dateOfPayment = dateOfPayment;
            this.transactionNumber = transactionNumber;
            this.prevailingRateAtPaymentDate = prevailingRateAtPaymentDate;
    }

}
