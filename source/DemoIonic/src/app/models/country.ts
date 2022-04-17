import { Service } from "./service";

export class Country {
    name: string | undefined;
    services: Service[] | undefined;
    countryId: number | undefined;

    constructor(countryId?: number, name?: string) {
            this.countryId = countryId;
            this.name = name;
    }
}
