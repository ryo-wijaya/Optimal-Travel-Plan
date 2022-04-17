import { Customer } from "./customer";

export class CustomerHandler {
    customer: Customer | undefined;
    password: string | undefined;

    constructor(customer?: Customer, password?: string) {
        this.customer = customer;
        this.password = password;
    }
}
