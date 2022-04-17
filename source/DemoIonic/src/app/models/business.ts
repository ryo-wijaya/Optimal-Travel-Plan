import { Service } from "./service";

export class Business {
    companyName: string | undefined;
    companyWebsite: string | undefined;
    companyNumber: string | undefined;
    headquarterAddress: string | undefined;
    username: string | undefined;
    password: string | undefined;
    email: string | undefined;
    services: Service[] | undefined;

    constructor(companyName?: string , companyWebsite?: string,
        companyNumber?: string, headquarterAddress?: string,
        username?: string, password?: string, email?: string) {
            this.companyName = companyName;
            this.companyWebsite = companyWebsite;
            this.companyNumber = companyNumber;
            this.headquarterAddress = headquarterAddress;
            this.username = username;
            this.password = password;
            this.email = email;
    }


}
