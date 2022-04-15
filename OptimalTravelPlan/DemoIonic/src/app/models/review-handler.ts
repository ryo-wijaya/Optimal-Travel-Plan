
import { Customer } from "./customer";
import { Review } from "./review";


export class ReviewHandler {
    customer: Customer | null;
    review: Review | null;
    BookingId: number | null;
    password: string | null;
    reviewId: number | null;
}
