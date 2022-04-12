import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'index',
    pathMatch: 'full'
  },
  {
    path: 'index',
    loadChildren: () => import('./index/index.module').then(m => m.IndexPageModule)
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginPageModule)
  },
  {
    path: 'registration',
    loadChildren: () => import('./registration/registration.module').then(m => m.RegistrationPageModule)
  },
  {
    path: 'client/home',
    loadChildren: () => import('./Client/home/home.module').then( m => m.HomePageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'client/travelItineraries',
    loadChildren: () => import('./Client/travel-itineraries/travel-itineraries.module').then( m => m.TravelItinerariesPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'client/supportRequests',
    loadChildren: () => import('./Client/support-requests/support-requests.module').then( m => m.SupportRequestsPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'client/services',
    loadChildren: () => import('./Client/services/services.module').then( m => m.ServicesPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'accessDenied',
    loadChildren: () => import('./access-denied/access-denied.module').then( m => m.AccessDeniedPageModule)
  },
  {
    path: 'client/accountDetails',
    loadChildren: () => import('./Client/account-details/account-details.module').then( m => m.AccountDetailsPageModule)
  },
  {
    path: 'travelItineraryDetails',
    loadChildren: () => import('./travel-itinerary-details/travel-itinerary-details.module').then( m => m.TravelItineraryDetailsPageModule)
  },
  {
    path: 'client/view-support-requests',
    loadChildren: () => import('./Client/view-support-requests/view-support-requests.module').then( m => m.ViewSupportRequestsPageModule)
  }

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
