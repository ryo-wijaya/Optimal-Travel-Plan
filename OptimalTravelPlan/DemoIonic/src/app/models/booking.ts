import { TravelItinerary } from "./travel-itinerary";
import { Service } from "./service";
import { PaymentTransaction } from "./payment-transaction";
import { SupportRequest } from "./support-request";
import { Review } from "./review";

export class Booking {
    bookingId: number | undefined;
    startDate: Date | undefined;
    endDate: Date | undefined;
    travelItinerary: TravelItinerary | undefined;
    service: Service | undefined;
    paymentTransaction: PaymentTransaction | undefined | null;
    supportRequest: SupportRequest | undefined | null;
    review:Review | undefined | null;

    constructor(bookingId?: number, startDate?: Date, endDate?: Date, 
        travelItinerary?: TravelItinerary, service?: Service) {
            this.bookingId = bookingId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.travelItinerary = travelItinerary;
            this.service = service;
    }

}
