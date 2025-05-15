import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreFileComponent } from './cre-file.component';

describe('CreFileComponent', () => {
  let component: CreFileComponent;
  let fixture: ComponentFixture<CreFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreFileComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
