import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  search(keywords: any) {
    return this.http.post(`${this.baseUrl}/api/search/simple`, keywords);
  }

  advancedSearch(keywords: any) {
    return this.http.post(`${this.baseUrl}/api/search/advanced`, keywords);
  }

  phrasequery(query: any) {
    return this.http.post(`${this.baseUrl}/api/search/phrase`, query);
  }

  downloadFile(serverFilename: string) {
    return this.http.get(`${this.baseUrl}/api/file/${serverFilename}`, {responseType: 'blob'});
  }

  fieldSearch(keywords: any) {
    return this.http.post(`${this.baseUrl}/api/search/field`, keywords);
  }

  geoSearch(obj: any) {
    return this.http.post(`${this.baseUrl}/api/search/geo`, obj);
  }
}
