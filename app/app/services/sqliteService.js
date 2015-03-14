var sqliteService = {
	db: openDatabase('aptmanage', '1.0', 'aptmanage app database', 64 * 1024),

	createTables: function() {
		this.db.transaction(function(tx) {
			tx.executeSql("create table appointments (id integer unique, date timestamp, therapist_name text, therapist_id text)")
		})
	},

	getAppointments: function(startDate, callback) {
		if (!startDate) {
			startDate = (new Date()).getTime();
		}
		this.db.transaction(function(tx) {
			tx.executeSql("select * from appointments", [], function(tx, results) {
				var appointments = []
				for (var i = 0; i < results.rows.length; i++) {
					var apt = results.rows.item(i)
					appointments.push(apt)
				}
				if (callback) callback(appointments)
			})
		})
	},

	addAppointments: function(appointments) {
		this.db.transaction(function(tx) {
			for (var i = appointments.length - 1; i >= 0; i--) {
				var appointment = appointments[i]
				tx.executeSql("insert into appointments (id, date, therapist_name, therapist_id) values (?,?,?,?)", [appointment.id,appointment.date,appointment.therapist.full_name,appointment.therapist.id])
			}
		})
	},

	removeAppointmentById: function(id, callback) {
		this.db.transaction(function(tx) {
			tx.executeSql("delete from appointments where id = ?", [id], function() {
				if (callback)callback()
			})
		})
	},

	updateAppointment: function(id, newTime, callback) {
		this.db.transaction(function(tx) {
			tx.executeSql("update appointments set date = ? where id = ?", [newTime, id], function(tx, res) {
				if (callback) callback()
			})
		})
	}
}

sqliteService.createTables()