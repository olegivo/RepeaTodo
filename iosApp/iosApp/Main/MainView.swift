//
//  MainView.swift
//  iosApp
//
//  Created by Олег Иващенко on 16.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MainView: View {
    @Environment(\.isPreview) var isPreview
    
    @StateObject
    private var viewModel: MainViewModelObservableObject
    @StateObject
    private var navigator: MainNavigatorObservableObject

    var body: some View {
        NavigationView {
            VStack() {
                Spacer()
                Divider()
                TasksListView.factory(isPreview: isPreview)
                Divider()
                AddTaskInlinedView.factory(isPreview: isPreview)
                    .padding()
            }
        }
        .navigationTitle("Todos")
        .handleNavigation($navigator.navigationDirection)
    }
    
    static func factory(isPreview: Bool = false) -> MainView {
        let di = MainComponent()
        return MainView(
            viewModel: (isPreview ? FakeMainViewModel() : di.mainViewModel()).asObservableObject(),
            navigator: (isPreview ? FakeMainNavigator() : di.mainNavigator()).asObservableObject()
        )
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView.factory(isPreview: true)
    }
}
