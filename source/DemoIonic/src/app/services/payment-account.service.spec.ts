import { TestBed } from '@angular/core/testing';

import { PaymentAccountService } from './payment-account.service';

describe('PaymentAccountService', () => {
  let service: PaymentAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentAccountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
