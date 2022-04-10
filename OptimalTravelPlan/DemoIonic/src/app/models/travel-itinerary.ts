import { Country } from "./country";
import { Customer } from "./customer";
import { Booking } from "./booking";

export class TravelItinerary {
    travelItineraryId: number | undefined;
    startDate: Date | undefined;
    endDate: Date | undefined;
    customer: Customer | undefined;
    country: Country | undefined;
    bookings: Booking[] | undefined;

    constructor(travelItineraryId?: number, startDate?: Date, endDate?: Date) {
        this.travelItineraryId = travelItineraryId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}


