import { Booking } from "./booking";

export class SupportRequest {
    supportRequestId: number | undefined;
    requestDetails: string | undefined;
    resolved: boolean = false;
    requestCreationDate: Date | undefined;
    booking: Booking | undefined;

    constructor(supportRequestId?: number, requestDetails?: string, requestCreationDate?: Date) {
        this.supportRequestId = supportRequestId;
        this.requestDetails = requestDetails;
        this.requestCreationDate = requestCreationDate;
    }
}
