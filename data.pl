distance(Id1, Id2, Time, Dist) :-
	node(Id1, Line_id, X1, Y1), node(Id2, Line_id, X2, Y2),
	D is (X1-X2)^2+(Y1-Y2)^2,
	line(Line_id, Limit, _),
	(traffic(Line_id, Low, High, Status), Time >= Low, Time < High ->
	Dist is D / Limit / Status
	; Dist is D / Limit).
	
can_ride(client(X, Y, Xd, Yd, _, People, Language), Id) :-
	taxi(_, _, Id, Capacity, Language, _, LongD),
	People =< Capacity,
	D is sqrt((X-Xd)^2+(Y-Yd)^2),
	(LongD = no -> D < 0.45 ; true).


closestNode(X, Y, Id_ret) :-
	findall(node(Id, Line_id, Xn, Yn), node(Id, Line_id, Xn, Yn), L),
	once(node(Idt, _, Xt, Yt)),
	Min is (X-Xt)^2+(Y-Yt)^2,
	find_min(L, X, Y, Min, Idt, Id_ret).
	
find_min([node(Id, _, Xn, Yn)], X, Y, Min_temp, Id_temp, Id_ret) :-
	Min is (X-Xn)^2+(Y-Yn)^2,
	(Min < Min_temp -> Id_ret = Id ; Id_ret = Id_temp).
	
find_min([node(Id, _, Xn, Yn)|Tail], X, Y, Min_temp, Id_temp, Id_ret) :-
	Min is (X-Xn)^2+(Y-Yn)^2,
	(Min < Min_temp -> find_min(Tail, X, Y, Min, Id, Id_ret) ; find_min(Tail, X, Y, Min_temp, Id_temp, Id_ret)).
	
	


