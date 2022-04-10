import { PaymentType } from "./PaymentType-enum";

export class PaymentAccount {
    paymentAccountId: number | undefined;
    accountNumber: string | undefined;
    cardExpirationDate: Date | undefined;
    ccv: String | undefined;
    paymentType: PaymentType | undefined;
    enabled: boolean | undefined;

    constructor(paymentAccountId?: number, accountNumber?: string,
        cardExpirationDate?: Date, ccv?: string, 
        paymentType?: PaymentType, enabled?: boolean) {
            this.paymentAccountId = paymentAccountId;
            this.accountNumber = accountNumber;
            this.cardExpirationDate = cardExpirationDate;
            this.ccv = ccv;
            this.paymentType = paymentType;
            this.enabled = enabled;
    }

}
