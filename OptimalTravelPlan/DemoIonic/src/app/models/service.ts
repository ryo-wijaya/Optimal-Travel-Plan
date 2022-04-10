import { Booking } from "./booking";
import { Business } from "./business";
import { Country } from "./country";
import { ServiceRate } from "./service-rate";
import { ServiceType } from "./serviceType-enum";
import { Tag } from "./tag";

export class Service {
    serviceId: number | undefined;
    serviceName: string | undefined;
    serviceType: ServiceType | undefined;
    address: string | undefined;
    active: boolean = true;
    requireVaccination: boolean = false;
    rating: number = 0;
    totalNumOfRatings: number = 0;
    business: Business | undefined;
    rates: ServiceRate[] | undefined;
    country: Country | undefined;
    bookings: Booking[] | undefined;
    tags: Tag[] | undefined;

    constructor(serviceId?: number, serviceName?: string, serviceType?: ServiceType, address?: string) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.address = address;
    }
}
