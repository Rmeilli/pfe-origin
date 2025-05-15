import { TestBed } from '@angular/core/testing';

import { CreFileService } from './cre-file.service';

describe('CreFileService', () => {
  let service: CreFileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CreFileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
