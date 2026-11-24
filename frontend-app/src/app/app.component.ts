import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [JsonPipe],
  template: `
    <main style="font-family: system-ui, sans-serif; max-width: 640px; margin: 3rem auto;">
      <h1>GKE + GCS Architecture Demo</h1>
      <button (click)="fetchData()" style="padding: 0.6rem 1.2rem; cursor: pointer;">
        Call Backend (app-a &rarr; app-b)
      </button>
      @if (response()) {
        <pre style="background: #f4f4f5; padding: 1rem; margin-top: 1rem; border-radius: 6px;">{{ response() | json }}</pre>
      }
      @if (error()) {
        <p style="color: #dc2626;">{{ error() }}</p>
      }
    </main>
  `
})
export class AppComponent {
  private http = inject(HttpClient);
  response = signal<unknown>(null);
  error = signal<string | null>(null);

  fetchData() {
    this.error.set(null);
    // Relative path routed by Global External LB directly to app-a NEG
    this.http.get('/api/aggregate').subscribe({
      next: (data) => this.response.set(data),
      error: (err) => this.error.set(`Request failed: ${err.status} ${err.statusText}`)
    });
  }
}