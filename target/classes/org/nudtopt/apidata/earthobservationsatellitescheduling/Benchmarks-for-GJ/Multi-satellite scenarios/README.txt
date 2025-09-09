Each scenario includes two csv-formatted files：

1. Imaging VTWs data.csv：

Column	Column name	Specification
1	SatelliteID	The unique ID of the satellite that provides this VTW
2	TaskID		The unique ID of the task that needs to be imaged
3	OrbitNum	The unique ID (in case of the same satellite) of the orbit that provides this VTW
4	Priority		The priority of the task, namely the price of the image
5	RecordRatio	The ratio between the downlink and imaging durations of this task at the “record or playback” mode
6	WriteSpeed	The data (in Gigabyte) produced by the imaging event of this task per second
7	ImageNum	The number of images in the imaging event for this task
8	StationID		The specified station for the downlink event of this task (unspecified if null)
9	WindowID	The specified VTW for the imaging event of this task (unspecified if null)
10	IsShadow		Whether the task is located in the shadow (not if null)
11	CameraType	The type of the camera
12	WinBeginTime	The begin-time of the VTW for the imaging event of this task (in second)
13	WinEndTime	The end-time of the VTW for the imaging event of this task (in second)
14	Duration		The duration of the imaging event of this task (in second)
15	PitchAngleList	A list of satellite pitch angles (separated by semicolons) per second in the VTW (excluding an imaging duration)
16	RollAngleList	A list of satellite roll angles (separated by semicolons) per second in the VTW (excluding an imaging duration)
17	YawAngleList	A list of satellite yaw angles (separated by semicolons) per second in the VTW (excluding an imaging duration)


2. Downlink VTWs data.csv：

Column	Column name	Specification
1	StationID		The unique ID of the station that provides this VTW
2	SatelliteID	The unique ID of the satellite that provides this VTW
3	WindowID	The unique ID (in case of the same satellite) code of the VTW
4	OrbitNum	The unique ID (in case of the same satellite) of the orbit that provides this VTW
5	IsShadow		Whether the station is located in the shadow (not if 0)
6	IsRelay		Whether the station is a relay satellite (not if 0)
7	WinBeginTime	The begin-time of this VTW
8	WinEndTime	The end-time of this VTW
9	Duration		The duration of this VTW

