import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class DateInterceptor implements HttpInterceptor {
  private isoDateFormat = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d*)?(?:[-+]\d{2}:?\d{2})?Z?$/;

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      map((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          const body = event.body;
          this.convertDates(body);
          return event.clone({ body });
        }
        return event;
      })
    );
  }

  private convertDates(body: any) {
    if (body === null || body === undefined || typeof body !== 'object') {
      return body;
    }

    for (const key of Object.keys(body)) {
      const value = body[key];
      if (this.isIsoDateString(value)) {
        body[key] = new Date(value);
      } else if (this.isDateArray(value)) {
        // Convert array format [year, month, day, hour, minute, second, millisecond] to Date
        body[key] = new Date(
          value[0], // year
          value[1] - 1, // month (0-based)
          value[2], // day
          value[3] || 0, // hour
          value[4] || 0, // minute
          value[5] || 0, // second
          value[6] ? Math.floor(value[6] / 1000000) : 0 // convert nanoseconds to milliseconds
        );
      } else if (typeof value === 'object') {
        this.convertDates(value);
      }
    }
  }

  private isIsoDateString(value: any): boolean {
    if (typeof value !== 'string') {
      return false;
    }
    return this.isoDateFormat.test(value);
  }

  private isDateArray(value: any): boolean {
    return Array.isArray(value) && 
           value.length >= 3 && // at least year, month, day
           value.length <= 7 && // up to milliseconds
           value.every(n => typeof n === 'number');
  }
} 