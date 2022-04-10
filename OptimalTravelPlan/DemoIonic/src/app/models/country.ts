import { Service } from "./service";

export class Country {
    name: string | undefined;
    services: Service[] | undefined;
    countryId: Number | undefined;

    constructor(countryId?: Number, name?: string) {
            this.countryId = countryId;
            this.name = name;
    }
}
