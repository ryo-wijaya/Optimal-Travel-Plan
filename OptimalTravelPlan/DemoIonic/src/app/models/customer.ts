import { Tag } from "./tag";
import { TravelItinerary } from "./travel-itinerary";
import { PaymentAccount } from "./payment-account";

export class Customer {
    favouriteTags: Tag[] | undefined;
    travelItineraries: TravelItinerary[] | undefined;
    paymentAccounts: PaymentAccount[] | undefined;
    name: string | undefined;
    mobile: string | undefined;
    passportNumber: string | undefined;
    email: string | undefined;
    vaccinationstatus: boolean | undefined;
    username: string | undefined;
    password: string | undefined;

    constructor(name?: string, mobile?: string, passportNumber?: string,
        email?: string, vaccinationStatus?: boolean,
        username?: string, password?: string) {
            this.name = name;
            this.mobile = mobile;
            this.passportNumber = passportNumber;
            this.email = email;
            this.vaccinationstatus = vaccinationStatus;
            this.username = username;
            this.password = password;
    }


}
