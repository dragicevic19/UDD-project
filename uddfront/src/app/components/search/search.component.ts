import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SearchService } from 'src/app/services/search.service';
import * as FileSaver from 'file-saver';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {


  query = "";
  fileInfos : any = [];
  searchDone = false;
  isAdvanced = false;
  isFieldSearch = false;
  addressQuery = "";
  radiusQuery = "";

  // currentPage = 1;  // Trenutna stranica
  // row = 4;  // Veličina stranice
  // totalItems = 0;  // Ukupan broj poruka
  // totalPages = 0;
  
  constructor(private searchService: SearchService,
     private router: Router,
     private toastrService: ToastrService) {}

  ngOnInit(): void {

  }

  onAdvancedSearchClicked() {
    this.isAdvanced = !this.isAdvanced;
    this.isFieldSearch = false;
  }
  onFieldSearchClick() {
    this.isFieldSearch = !this.isFieldSearch;
    this.isAdvanced = false;
  }

  onSubmit() {
    if (this.query.startsWith('"') && this.query.endsWith('"')) {
      this.phrasequery(this.query.replaceAll('"', ''));
      return;
    }
    if (this.isFieldSearch){
      const obj = {keywords: this.query.split(':')};
      this.fieldSearch(obj);
      return;
    }
    const tokens = this.query.split(' '); 
    const obj = {keywords: tokens};
    if (this.isAdvanced) {
      this.advancedSearch(obj);
    }
    else {
      this.simpleSearch(obj);
    }
  }

  onGeoLocSearch() {
    const obj = {address: this.addressQuery, radius: this.radiusQuery};
    this.searchService.geoSearch(obj).subscribe({
      next: (res: any) => {
        this.fileInfos = res.content;
        this.searchDone = true;
      },
      error: (err) => {
        this.toastrService.error(err.error.message);
        console.log(err);
      }
    })
  }

  fieldSearch(obj: any) {
     this.searchService.fieldSearch(obj).subscribe({
      next: (res: any) => {
        this.fileInfos = res.content;
        this.searchDone = true;
      },
      error: (err) => {
        this.toastrService.error(err.error.message);
        console.log(err);
      }
    })
  }

  simpleSearch(obj: any) {
    this.searchService.search(obj).subscribe({
      next: (res: any) => {
        this.fileInfos = res.content;
        this.searchDone = true;
      },
      error: (err) => {
        this.toastrService.error(err.error.message);
        console.log(err);
      }
    })
  }

  advancedSearch(obj: any) {
    this.searchService.advancedSearch(obj).subscribe({
      next: (res: any) => {
        this.fileInfos = res.content;
        this.searchDone = true;
      },
      error: (err) => {
        this.toastrService.error(err.error.message);
        console.log(err);
      }
    })
  }

  phrasequery(query: string) {
    const obj = {query: query}
    this.searchService.phrasequery(obj).subscribe({
      next: (res: any) => {
        this.fileInfos = res.content;
        this.searchDone = true;
      },
      error: (err: any) => {
        this.toastrService.error(err.error.message);
        console.log(err);
      }
    })
  }

  download(serverFilename: string) {
    this.searchService.downloadFile(serverFilename).subscribe({
      next: (res: any) => {
        const blob = new Blob([res], { type: 'application/octet-stream' });
        FileSaver.saveAs(blob, serverFilename);
      },
      error: (err) => {
        console.log(err);
      }
    })
  }
  
  onPageChange(pageNumber: number) {
    this.onSubmit();
  }

  getPageArray(totalPages: number): number[] {
    return Array(totalPages).fill(0).map((x, i) => i + 1);
  }
}
