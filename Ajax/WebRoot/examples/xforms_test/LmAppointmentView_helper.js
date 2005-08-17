if (window.LmAppointmentView) {
	var XM = new XModel(LmAppointmentView.appointmentModel);
	var apptInstance = 
		{
				id : "",
				uid : -1,
				type : null,
				name : "Name",
				startDate : new Date(),
				endDate : new Date(new Date().getTime() + (30*60*1000)),
				transparency : "FR",
				allDayEvent : '0',
				exception : false,
				recurring : false,
				alarm : false,
				otherAttendees : false,
				location : "location",
				notes : null,
				repeatType : "NON",
//				repeatDisplay : null,
				repeatCustom : 0,
				repeatCustomCount : 1,
				repeatCustomType : 'O', // (S)pecific, (O)rdinal
//				repeatCustomOrdinal : '1',
//				repeatCustomDayOfWeek : 'DAY', //(d|wd|we)|((Su|Mo|Tu|We|Th|Fr|Sa
//				repeatWeeklyDays : 'SUNDAY', //Su|Mo|Tu|We|Th|Fr|Sa
//				repeatMonthlyDayList : '1',
//				repeatYearlyMonthsList : '1',
//				repeatEnd : null,
//				repeatEndDate : new Date(),
				repeatEndCount : 1,
				repeatEndType : 'N'
	};
	var formAttr = LmAppointmentView.prototype.getAppointmentForm();
	registerForm("Appointment Edit", new XForm(formAttr, XM), {"Sample":apptInstance});
}
