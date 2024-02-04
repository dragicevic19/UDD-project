import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { SearchComponent } from './components/search/search.component';

const routes: Routes = [
  {
    path: '', component: FileUploadComponent
  },
  {
    path: 'search', component: SearchComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
