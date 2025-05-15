import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreValidationComponent } from './cre-validation.component';

describe('CreValidationComponent', () => {
  let component: CreValidationComponent;
  let fixture: ComponentFixture<CreValidationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreValidationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
