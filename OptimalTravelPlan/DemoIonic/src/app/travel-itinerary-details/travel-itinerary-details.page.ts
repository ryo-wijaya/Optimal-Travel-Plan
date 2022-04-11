import { Component, OnInit } from '@angular/core';
import { AlertController, ModalController } from '@ionic/angular';



@Component({
  selector: 'app-travel-itinerary-details',
  templateUrl: './travel-itinerary-details.page.html',
  styleUrls: ['./travel-itinerary-details.page.scss'],
})
export class TravelItineraryDetailsPage implements OnInit {

  events: any[] | null;
  options: any;
  header: any;


  constructor() { }

  ngOnInit() {

    this.options = {
      headerToolbar: {
        initialDate : '2019-01-01',
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,timeGridDay'
      },
      editable: true,
      selectable: true,
      selectMirror: true,
      dayMaxEvents: true
    };
  }

}


