import { Tag } from "./tag";

export class ServiceByTagHandler {
    username: string | undefined;
    password: string | undefined;
    tagIds: Tag[] | undefined;

    constructor(username?: string, password?: string) {
        this.username = username;
        this.password = password;
    }
}
