import { TestBed } from '@angular/core/testing';

import {StructureCREService} from './structure-cre.service';
import {ChampCREComponent} from "../components/champ-cre/champ-cre.component";

describe('StructureCreService', () => {
  let service: StructureCREService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StructureCREService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
