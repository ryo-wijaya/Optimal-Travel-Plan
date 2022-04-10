import { Booking } from "./booking";

export class Review {
    reviewId: number | undefined;
    rating: number | undefined;
    content: string | undefined;
    businessReply: string | undefined;
    booking: Booking | undefined;

    constructor(reviewId?: number, rating?: number, content?: string, businessReply?: string) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.content = content;
        this.businessReply = businessReply;
    }
}
