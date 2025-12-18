function getCurrentLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            const userLocation = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            if (window.map) {
                window.map.center(userLocation);
            }
        }, function (error) {
            console.error("Error getting user location:", error);
        });
    } else {
        console.error("Geolocation is not supported");
    }
}

document.getElementById("current-location").addEventListener("click", getCurrentLocation);

function initAutocomplete() {

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            const userLocation = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };

            const map = new google.maps.Map(document.getElementById("map"), {
                center: userLocation,
                zoom: 15,
                mapTypeId: "roadmap",
                styles: [
                    { featureType: "poi", elementType: "labels", stylers: [{ visibility: "off" }] }
                ]
            });
            window.map = map;

            const input = document.getElementById("pac-input");
            const searchBox = new google.maps.places.SearchBox(input);

            map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

            map.addListener("bounds_changed", () => {
                searchBox.setBounds(map.getBounds());
            });

            let markers = [];
            let infowindow;

            function myClickListener(place, marker) {
                const placeName = place.name;
                const address = place.formatted_address || "";

                // Styles based on Tailwind classes
                const containerStyle = 'min-width: 260px; padding: 4px;';

                const request = {
                    placeId: place.place_id,
                    fields: ["opening_hours", "formatted_phone_number"]
                };

                const service = new google.maps.places.PlacesService(map);
                service.getDetails(request, (placeResult, status) => {
                    if (status === google.maps.places.PlacesServiceStatus.OK) {

                        // 1. Check Open Status
                        let isOpen = false;
                        let statusText = "정보 없음";
                        let statusClass = "bg-slate-700 text-slate-300 border border-slate-600";

                        if (placeResult.opening_hours) {
                            try {
                                isOpen = placeResult.opening_hours.isOpen();
                            } catch (e) {
                                if (placeResult.opening_hours.open_now) isOpen = true;
                            }

                            if (isOpen) {
                                statusText = "영업중";
                                statusClass = "bg-emerald-900/50 text-emerald-300 border border-emerald-700/50";
                            } else {
                                statusText = "영업종료";
                                statusClass = "bg-rose-900/50 text-rose-300 border border-rose-700/50";
                            }
                        }

                        // 2. Calculate Time Remaining
                        let remainingTimeStr = "";
                        let progressBarWidth = "0%";
                        let progressColor = "bg-slate-600";

                        if (isOpen && placeResult.opening_hours && placeResult.opening_hours.periods) {
                            const today = new Date();
                            const currentDayIndex = today.getDay();
                            const period = placeResult.opening_hours.periods.find(p => p.close && p.close.day === currentDayIndex);

                            if (period) {
                                let closingHour = parseInt(period.close.time.substring(0, 2));
                                const closingMinute = parseInt(period.close.time.substring(2, 4));

                                const closingTime = new Date();
                                closingTime.setHours(closingHour, closingMinute, 0);

                                if (closingTime < today) {
                                    closingTime.setDate(closingTime.getDate() + 1);
                                }

                                const timeDiff = closingTime - today;
                                if (timeDiff > 0) {
                                    const hours = Math.floor(timeDiff / (1000 * 60 * 60));
                                    const minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60));
                                    remainingTimeStr = `${hours}시간 ${minutes}분`;

                                    if (hours >= 3) {
                                        progressColor = "bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.5)]";
                                        progressBarWidth = "100%";
                                    } else if (hours >= 1) {
                                        progressColor = "bg-yellow-500 shadow-[0_0_10px_rgba(234,179,8,0.5)]";
                                        progressBarWidth = "60%";
                                    } else {
                                        progressColor = "bg-rose-500 shadow-[0_0_10px_rgba(244,63,94,0.5)]";
                                        progressBarWidth = "20%";
                                    }
                                }
                            }
                        }

                        // Construct HTML
                        const contentDiv = document.createElement("div");
                        contentDiv.className = "text-left font-sans bg-slate-800 text-white p-1 min-w-[260px]";
                        contentDiv.innerHTML = `
                            <div class="mb-3">
                                <h3 class="text-lg font-bold text-white leading-tight break-keep" style="color: #f1f5f9;">${placeName}</h3>
                                <p class="text-xs text-slate-400 mt-1">${address}</p>
                            </div>
                            
                            <div class="flex items-center gap-2 mb-4">
                                <span class="px-2.5 py-1 rounded-full text-xs font-bold ${statusClass} inline-flex items-center">
                                    <span class="w-1.5 h-1.5 rounded-full ${isOpen ? 'bg-emerald-400' : 'bg-rose-400'} mr-1.5 shadow-sm"></span>
                                    ${statusText}
                                </span>
                            </div>

                            ${remainingTimeStr ? `
                            <div class="bg-slate-700/50 rounded-lg p-2.5 border border-slate-700">
                                <div class="flex justify-between items-end mb-2">
                                     <span class="text-[11px] text-slate-400 font-medium">마감까지</span>
                                     <span class="text-sm font-bold text-emerald-300 font-mono tracking-wide">${remainingTimeStr}</span>
                                </div>
                                <div class="w-full bg-slate-700 rounded-full h-1.5 overflow-hidden">
                                    <div class="${progressColor} h-1.5 rounded-full transition-all duration-500" style="width: ${progressBarWidth}"></div>
                                </div>
                            </div>
                            ` : ''}
                            
                            ${!remainingTimeStr && isOpen ? `<p class="text-[11px] text-slate-500 py-2">영업 시간 정보가 제한적입니다.</p>` : ''}
                        `;

                        if (infowindow) {
                            infowindow.close();
                        }
                        infowindow = new google.maps.InfoWindow({
                            content: contentDiv,
                            maxWidth: 320
                        });
                        infowindow.open(map, marker);
                    }
                });
            }

            searchBox.addListener("places_changed", () => {
                const places = searchBox.getPlaces();
                if (places.length == 0) return;

                // Clear old markers
                markers.forEach((marker) => marker.setMap(null));
                markers = [];

                const bounds = new google.maps.LatLngBounds();

                // Filter only OPEN/OPERATIONAL places
                const openPlaces = places.filter(place => {
                    // 1. Check Business Status if supported
                    if (place.business_status && place.business_status !== 'OPERATIONAL') {
                        return false;
                    }
                    // 2. Check Opening Hours (Open Now)
                    // Note: SearchBox results usually provide partial info. 
                    // If opening_hours is undefined, we generally show it to be safe, 
                    // BUT user explicitly asked to filter closed places, so let's be strict if info exists.
                    if (place.opening_hours && place.opening_hours.open_now === false) {
                        return false;
                    }
                    return true;
                });

                if (openPlaces.length === 0) {
                    console.log("No open places found in this area.");
                    return;
                }

                openPlaces.forEach((place) => {
                    if (!place.geometry || !place.geometry.location) {
                        return;
                    }

                    const icon = {
                        url: place.icon,
                        size: new google.maps.Size(71, 71),
                        origin: new google.maps.Point(0, 0),
                        anchor: new google.maps.Point(17, 34),
                        scaledSize: new google.maps.Size(25, 25),
                    };

                    const marker = new google.maps.Marker({
                        map,
                        icon,
                        title: place.name,
                        position: place.geometry.location,
                    });

                    marker.addListener("click", () => {
                        myClickListener(place, marker);
                    });

                    markers.push(marker);

                    if (place.geometry.viewport) {
                        bounds.union(place.geometry.viewport);
                    } else {
                        bounds.extend(place.geometry.location);
                    }
                });
                map.fitBounds(bounds);
            });

        }, function (error) {
            console.error("Error getting user location:", error);
        });
    } else {
        console.error("Geolocation is not supported");
    }
}

window.initAutocomplete = initAutocomplete;