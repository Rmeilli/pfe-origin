import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StructureCREComponent } from './structure-cre.component';

describe('StructureCreComponent', () => {
  let component: StructureCREComponent;
  let fixture: ComponentFixture<StructureCREComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StructureCREComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StructureCREComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
