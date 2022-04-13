import { Customer } from "./customer";
import { TravelItinerary } from "./travel-itinerary";

export class TravelItineraryHandler {
    newCountryId: number | undefined;
    password: string | undefined;
    customer: Customer | undefined;
    travelItinerary: TravelItinerary | undefined;
    travelItineraryId: number | undefined;

    constructor(newCountryId?: number, password?: string) {
        this.newCountryId = newCountryId;
        this.password = password;
    }
}
