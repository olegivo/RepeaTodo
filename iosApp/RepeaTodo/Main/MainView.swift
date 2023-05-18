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
    @Environment(\.navigator) var navigator: MainNavigatorObservableObject
    @Environment(\.previewEnvironment) var previewEnvironment: PreviewEnvironment?

//    @StateObject private var viewModel: MainViewModelObservableObject

    var body: some View {
        NavigationView {
            VStack() {
                Spacer()
                Divider()
                TasksListView.factory(previewEnvironment)
                    .environmentObject(navigator)
                Divider()
                AddTaskInlinedView.factory(previewEnvironment)
                    .padding()
            }
        }
        .navigationTitle("Todos")
        .handleNavigation(navigator)
    }
    
    static func factory(_ previewEnvironment: PreviewEnvironment? = nil) -> some View {
        let di = MainComponent()
        let navigator = (previewEnvironment?.get() ?? di.mainNavigator()).asObservableObject()
        return MainView(
            //viewModel: (isPreview ? FakeMainViewModel() : di.mainViewModel()).asObservableObject()
        )
        .navigator(navigator)
//        .previewEnvironment(preview{ $0.mainScreenFakes() })
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView.factory(preview{ $0.mainScreenFakes() })
    }
}
