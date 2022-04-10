import { TestBed } from '@angular/core/testing';

import { SupportRequestService } from './support-request.service';

describe('SupportRequestService', () => {
  let service: SupportRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SupportRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
