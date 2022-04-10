import { Service } from "./service";

export class Tag {
    tagId: number | undefined;
    name: string | undefined;
    services: Service[] | undefined;

    constructor(tagId?: number, name?: string) {
        this.tagId = tagId;
        this.name = name;
    }
}
