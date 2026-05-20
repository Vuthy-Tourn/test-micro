
package kh.edu.istad.stadoor.gateway.valueobject.service;
public record ServiceName(
        String name
) {
    @Override
    public String toString() {
        return name.toString();
    }
}
