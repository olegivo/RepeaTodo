import Foundation
import SwiftUI

struct DropdownRow<Item>: View
where Item: Hashable {
    var option: Item
    var onOptionSelected: ((_ option: Item) -> Void)
    var textSelector: ((_ option: Item) -> String)
    
    var body: some View {
        Button(action: {
            onOptionSelected(self.option)
        }) {
            HStack {
                Text(textSelector(self.option))
                    .font(.system(size: 14))
                    .foregroundColor(Color.black)
                Spacer()
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 5)
    }
}

struct Dropdown<Item>: View
where Item: Hashable {
    
    var items: [Item]
    var textSelector: ((_ option: Item) -> String)
    var onSelected: ((_ option: Item) -> Void)
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                ForEach(self.items, id: \.self) { option in
                    DropdownRow(
                        option: option,
                        onOptionSelected: onSelected,
                        textSelector: textSelector
                    )
                }
            }
        }
        .frame(minHeight: CGFloat(items.count) * 30, maxHeight: 250)
        .padding(.vertical, 5)
        .background(Color.white)
        .cornerRadius(5)
        .overlay(
            RoundedRectangle(cornerRadius: 5)
                .stroke(Color.gray, lineWidth: 1)
        )
    }
}

struct DropdownSelector<Item>: View
where Item: Hashable {
    @State private var shouldShowDropdown = false
    var placeholder: String? = nil
    var items: [Item]
    @State var selectedItem: Item? = nil
    var textSelector: ((_ option: Item) -> String)
    var noneSelectedItem: Item? = nil
    var canClear: Bool = true
    var onSelected: ((_ option: Item) -> Void)
    private let buttonHeight: CGFloat = 45
    
    var body: some View {
        HStack {
            Button(action: {
                self.shouldShowDropdown.toggle()
            }) {
                HStack {
                    Text(getSelectedText())
                        .font(.system(size: 14))
                        .foregroundColor(selectedItem == nil ? Color.gray: Color.black)
                    
                    Spacer()
                    
                    Image(systemName: self.shouldShowDropdown ? "arrowtriangle.up.fill" : "arrowtriangle.down.fill")
                        .resizable()
                        .frame(width: 9, height: 5)
                        .font(Font.system(size: 9, weight: .medium))
                        .foregroundColor(Color.black)
                }
            }
            
            if canClear {
                Button(action: {
                    self.selectedItem = nil
                }) {
                    Image(systemName: "xmark")
                        .font(Font.system(size: 9, weight: .medium))
                        .foregroundColor(Color.black)
                }
                .padding(.leading)
            }
        }
        .padding(.horizontal)
        .cornerRadius(5)
        .frame(width: .infinity, height: self.buttonHeight)
        .overlay(
            RoundedRectangle(cornerRadius: 5)
                .stroke(Color.gray, lineWidth: 1)
        )
        .overlay(
            VStack {
                if self.shouldShowDropdown {
                    Spacer(minLength: buttonHeight + 10)
                    Dropdown(
                        items: self.items,
                        textSelector: textSelector,
                        onSelected: { option in
                            shouldShowDropdown = false
                            selectedItem = option
                            self.onSelected(option)
                        }
                    )
                }
            },
            alignment: .topLeading
        )
        .background(
            RoundedRectangle(cornerRadius: 5).fill(Color.white)
        )
        .zIndex(1)
    }
    
    private func getSelectedText() -> String {
        if let selectedItem = selectedItem ?? noneSelectedItem {
            return textSelector(selectedItem)
        } else {
            return placeholder ?? ""
        }
    }
}

struct DropdownSelector_Previews: PreviewProvider {
    static let items: [String] = [
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday"
    ]
    
    static var previews: some View {
        Group {
            DropdownSelector(
                items: items,
                textSelector: { $0 },
                noneSelectedItem: "Day of the week",
                onSelected: { option in
                    print(option)
                }
            )
            .padding(.horizontal)
        }
    }
}
