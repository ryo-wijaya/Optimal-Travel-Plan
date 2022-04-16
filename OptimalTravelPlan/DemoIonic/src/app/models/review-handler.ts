
import { Customer } from "./customer";
import { Review } from "./review";


export class ReviewHandler {
    customer: Customer | null;
    review: Review | null;
    bookingId: number | null;
    password: string | null;
    reviewId: number | null;
}
