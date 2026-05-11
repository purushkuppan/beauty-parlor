import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.auth.getToken();
    const authReq = token
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

    return next.handle(authReq).pipe(
      catchError(err => {
        if (err.status === 401 && !req.url.includes('/auth/')) {
          return this.auth.refreshToken().pipe(
            switchMap(res => {
              const retried = req.clone({ setHeaders: { Authorization: `Bearer ${res.accessToken}` } });
              return next.handle(retried);
            }),
            catchError(() => {
              this.auth.logout();
              return throwError(() => err);
            })
          );
        }
        return throwError(() => err);
      })
    );
  }
}
