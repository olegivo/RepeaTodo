//
//  NavigationDirection.swift
//  iosApp
//
//  Created by Олег Иващенко on 16.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import shared

enum NavigationStyle {
    case push
    case present
}

enum NavigationDirection: Equatable {
    case back
    case forward(destination: NavigationDestination, style: NavigationStyle)

    static func == (lhs: NavigationDirection, rhs: NavigationDirection) -> Bool {
        switch (lhs, rhs) {
        case (.back, .back):
            return true
        default:
            return false
        }
    }
}
