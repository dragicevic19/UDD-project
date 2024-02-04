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
  
  constructor(private searchService: SearchService,
     private router: Router,
     private toastrService: ToastrService) {}

  ngOnInit(): void {

  }

  onSubmit() {
    if (this.query.startsWith('"') && this.query.endsWith('"')) {
      this.phrasequery(this.query.replaceAll('"', ''));
      return;
    }
    const tokens = this.query.split(' '); 
    const obj = {keywords: tokens};
    // if (tokens.some(t => ['AND', 'OR', 'NOT'].includes(t.toUpperCase()))) {
    if (this.isAdvanced) {
      this.advancedSearch(obj);
    }
    else {
      this.simpleSearch(obj);
    }
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
}
