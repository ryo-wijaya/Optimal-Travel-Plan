import { TravelItinerary } from "./travel-itinerary";
import { Service } from "./service";
import { PaymentTransaction } from "./payment-transaction";
import { SupportRequest } from "./support-request";

export class Booking {
    bookingId: Number | undefined;
    startDate: Date | undefined;
    endDate: Date | undefined;
    travelItinerary: TravelItinerary | undefined;
    service: Service | undefined;
    paymentTransaction: PaymentTransaction | undefined | null;
    supportRequest: SupportRequest | undefined | null;

    constructor(bookingId?: Number, startDate?: Date, endDate?: Date, 
        travelItinerary?: TravelItinerary, service?: Service) {
            this.bookingId = bookingId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.travelItinerary = travelItinerary;
            this.service = service;
    }

}
