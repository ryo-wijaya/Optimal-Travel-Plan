import { TestBed } from '@angular/core/testing';

import { TravelItineraryService } from './travel-itinerary.service';

describe('TravelItineraryService', () => {
  let service: TravelItineraryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TravelItineraryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
