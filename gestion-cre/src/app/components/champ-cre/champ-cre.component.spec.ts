import { ComponentFixture, TestBed } from '@angular/core/testing';

import {ChampCREComponent} from './champ-cre.component';

describe('ChampCreComponent', () => {
  let component: ChampCREComponent;
  let fixture: ComponentFixture<ChampCREComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChampCREComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChampCREComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
