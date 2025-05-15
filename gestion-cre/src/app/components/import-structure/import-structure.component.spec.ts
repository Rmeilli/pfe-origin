import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportStructureComponent } from './import-structure.component';

describe('ImportStructureComponent', () => {
  let component: ImportStructureComponent;
  let fixture: ComponentFixture<ImportStructureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ImportStructureComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ImportStructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
