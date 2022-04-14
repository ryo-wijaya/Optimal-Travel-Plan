import { Booking } from "./booking";
import { Customer } from "./customer";

export class BookingHandler {
    booking: Booking | undefined;
    customer: Customer | undefined;
    serviceId: Number | undefined | null;
    travelItineraryId: Number | undefined | null;
    password: string | undefined;
    cost:number | undefined;
    bookingId:number | undefined;




    constructor(booking?: Booking, customer?: Customer, 
        serviceId?: Number, travelItineraryId?: Number, 
        password?: string) {
            this.booking = booking;
            this.customer = customer;
            this.serviceId = serviceId;
            this.travelItineraryId = travelItineraryId;
            this.password = password;
    }
}
