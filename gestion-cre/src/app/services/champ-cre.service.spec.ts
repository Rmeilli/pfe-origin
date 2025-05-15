import { TestBed } from '@angular/core/testing';

import { ChampCREService } from './champ-cre.service';

describe('ChampCreService', () => {
  let service: ChampCREService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChampCREService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
