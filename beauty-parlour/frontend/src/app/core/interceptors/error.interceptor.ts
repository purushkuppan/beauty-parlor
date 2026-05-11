import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError(err => {
        const message = err.error?.error ?? err.message ?? 'An unexpected error occurred';
        console.error(`[HTTP ${err.status}]`, message);
        return throwError(() => new Error(message));
      })
    );
  }
}
