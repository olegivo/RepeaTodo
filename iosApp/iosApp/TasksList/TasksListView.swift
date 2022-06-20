//
//  TasksListView.swift
//  iosApp
//
//  Created by Олег Иващенко on 19.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

struct TasksListView: View {
    @Environment(\.isPreview) var isPreview
    
    @StateObject
    private var viewModel: TasksListViewModelObservableObject
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading) {
                ForEach(viewModel.state.tasks) { task in
                    Text(task.title)
                        .padding()
                }
            }
            .cornerRadius(CGFloat(12))
            .padding()
        }
    }
    
    static func factory(isPreview: Bool = false) -> TasksListView {
        return TasksListView(viewModel: (isPreview ? FakeTasksListViewModel() : TasksListComponent().tasksListViewModel()).asObservableObject())
    }
}

extension Task: Identifiable {
    public var id: String { title }
}

struct TasksListView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListView.factory(isPreview: true)
        }
        .background(Color.purple)
        .cornerRadius(CGFloat(12))
    }
}
