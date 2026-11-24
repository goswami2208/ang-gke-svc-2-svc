import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [JsonPipe],
  templateUrl: './app.html'
})
export class App {
  private http = inject(HttpClient);
  response = signal<unknown>(null);
  error = signal<string | null>(null);

  fetchData() {
    this.error.set(null);
    this.http.get('/api/aggregate').subscribe({
      next: (data) => this.response.set(data),
      error: (err) => this.error.set(`Request failed: ${err.status} ${err.statusText}`)
    });
  }
}