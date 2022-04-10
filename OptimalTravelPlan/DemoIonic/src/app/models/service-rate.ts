import { ChargeType } from "./ChargeType-enum";

export class ServiceRate {
    serviceRateId: number | undefined;
    startDate: Date | undefined;
    endDate: Date | undefined;
    price: number | undefined;
    enabled: boolean = true;
    chargeType: ChargeType | undefined;

    constructor(serviceRateId?: number, startDate?: Date, endDate?: Date, price?: number, chargeType?: ChargeType) {
        this.serviceRateId = serviceRateId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.chargeType = chargeType;
    }
}
